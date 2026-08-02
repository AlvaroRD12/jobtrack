import { mount } from '@vue/test-utils';
import { describe, it, expect, vi } from 'vitest';
import ApplicationsView from '../ApplicationsView.vue';

vi.mock('../../api/applications', () => ({
  listApplications: vi.fn().mockResolvedValue({ data: [] }),
  createApplication: vi.fn().mockResolvedValue({ data: { id: 1, company: 'Acme', position: 'Engineer', stage: 'Applied' } }),
  updateApplication: vi.fn().mockResolvedValue({ data: { id: 1, company: 'Acme', position: 'Engineer', stage: 'Interview' } }),
  archiveApplication: vi.fn().mockResolvedValue({ data: { id: 1, archived: true } }),
  deleteApplication: vi.fn().mockResolvedValue({ data: { id: 1 } })
}));

describe('ApplicationsView', () => {
  it('renders the form and creates an application', async () => {
    const wrapper = mount(ApplicationsView);
    expect(wrapper.text()).toContain('Applications');

    await wrapper.get('form').trigger('submit.prevent');
    expect(wrapper.text()).toContain('Acme');
  });
});
